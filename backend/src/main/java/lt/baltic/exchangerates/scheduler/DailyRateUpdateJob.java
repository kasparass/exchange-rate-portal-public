package lt.baltic.exchangerates.scheduler;

import lt.baltic.exchangerates.service.CurrencyService;
import lt.baltic.exchangerates.service.ExchangeRateService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DailyRateUpdateJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(DailyRateUpdateJob.class);

    @Autowired
    private ApplicationContext applicationContext;

    private CurrencyService currencyService;
    private ExchangeRateService exchangeRateService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Starting daily exchange rate update job");
        try {
            // Get the services from Spring context
            if (currencyService == null) {
                currencyService = applicationContext.getBean(CurrencyService.class);
            }
            if (exchangeRateService == null) {
                exchangeRateService = applicationContext.getBean(ExchangeRateService.class);
            }
            currencyService.updateCurrencyList();
            log.info("Currency list updated successfully");

            exchangeRateService.updateCurrentRates();
            log.info("Current rates updated successfully");
        } catch (Exception e) {
            log.error("Error updating exchange rates", e);
            throw new JobExecutionException(e);
        }
    }
}