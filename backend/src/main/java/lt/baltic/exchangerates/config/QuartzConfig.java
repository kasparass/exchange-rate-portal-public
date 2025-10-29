package lt.baltic.exchangerates.config;

import lt.baltic.exchangerates.model.Currency;
import lt.baltic.exchangerates.repository.ExchangeRateRepository;
import lt.baltic.exchangerates.scheduler.DailyRateUpdateJob;
import lt.baltic.exchangerates.scheduler.HistoricalDataLoadJob;
import org.springframework.beans.factory.annotation.Autowired;
import lt.baltic.exchangerates.service.CurrencyService;

import org.slf4j.Logger;
import org.quartz.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import jakarta.xml.bind.JAXBException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.quartz.spi.TriggerFiredBundle;

@Configuration
public class QuartzConfig {

    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {
        private AutowireCapableBeanFactory beanFactory;

        public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }

    @Value("${exchange-rates.scheduler.cron}")
    private String dailyUpdateCron;

    @Value("${exchange-rates.scheduler.trigger.delay}")
    private int triggerDelay;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    private static final Logger logger = LoggerFactory.getLogger(QuartzConfig.class);

    @Bean
    public JobDetail dailyRateUpdateJobDetail() {
        return JobBuilder.newJob(DailyRateUpdateJob.class)
                .withIdentity("dailyRateUpdateJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger dailyRateUpdateTrigger(JobDetail dailyRateUpdateJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(dailyRateUpdateJobDetail)
                .withIdentity("dailyRateUpdateTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(dailyUpdateCron))
                .build();
    }

    // This trigger will fire once when the application starts
    @Bean
    public Map<JobDetail, Trigger> initialHistoricalDataLoadTriggers(
            CurrencyService currencyService) throws JAXBException {
        Map<JobDetail, Trigger> triggers = new HashMap<>();

        logger.debug("Starting to create historical data load triggers");

        if (!exchangeRateRepository.isEmpty()) {
            logger.debug("No exchange rates found, loading initial data");
            return triggers;
        }

        // Load initial data for each currency
        List<Currency> currencies = currencyService.getAllCurrencies();
        if (currencies.isEmpty()) {
            logger.debug("No currencies found, updating currency list");
            currencyService.updateCurrencyList();
            currencies = currencyService.getAllCurrencies();
            logger.debug("Updated currency list, found {} currencies", currencies.size());
        }

        for (int i = 0; i < currencies.size(); i++) {
            Currency currency = currencies.get(i);
            String jobName = "historicalDataLoadJob_" + currency.getCode();

            // Create a unique JobDetail for each currency
            JobDetail jobDetail = JobBuilder.newJob(HistoricalDataLoadJob.class)
                    .withIdentity(jobName)
                    .storeDurably()
                    .build();

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("currencyCode", currency.getCode());

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity("historicalDataLoadTrigger_" + currency.getCode())
                    .startAt(new Date(System.currentTimeMillis() + (i * triggerDelay)))
                    .usingJobData(jobDataMap)
                    .build();

            triggers.put(jobDetail, trigger);

            logger.debug("Created trigger for currency: {}, scheduled to start in {} ms",
                    currency.getCode(), (i * triggerDelay));
        }

        return triggers;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            AutowireCapableBeanFactory autowireCapableBeanFactory,
            JobDetail dailyRateUpdateJobDetail,
            Trigger dailyRateUpdateTrigger,
            Map<JobDetail, Trigger> initialHistoricalDataLoadTriggers) {

        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();

        // Configure AutowiringSpringBeanJobFactory
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setBeanFactory(autowireCapableBeanFactory);
        schedulerFactory.setJobFactory(jobFactory);

        // Set up all triggers
        Map<JobDetail, Trigger> triggersMap = new HashMap<>();
        triggersMap.put(dailyRateUpdateJobDetail, dailyRateUpdateTrigger);
        triggersMap.putAll(initialHistoricalDataLoadTriggers);

        schedulerFactory.setJobDetails(triggersMap.keySet().toArray(new JobDetail[0]));
        schedulerFactory.setTriggers(triggersMap.values().toArray(new Trigger[0]));

        // Auto-start the scheduler
        schedulerFactory.setAutoStartup(true);

        return schedulerFactory;
    }
}