package lt.baltic.exchangerates.scheduler;

import lt.baltic.exchangerates.service.ExchangeRateService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class HistoricalDataLoadJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(HistoricalDataLoadJob.class);

    @Autowired
    private ApplicationContext applicationContext;

    private ExchangeRateService exchangeRateService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Starting historical data load job");
        try {
            // Get the ExchangeRateService from Spring context
            if (exchangeRateService == null) {
                exchangeRateService = applicationContext.getBean(ExchangeRateService.class);
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(90);

            String currencyCode = context.getMergedJobDataMap().getString("currencyCode");
            exchangeRateService.updateHistoricalRates(currencyCode, startDate, endDate);

            log.info("Historical rates update has been called for currency: {}", currencyCode);
        } catch (Exception e) {
            log.error("Error loading historical data", e);
            throw new JobExecutionException(e);
        }
    }
}