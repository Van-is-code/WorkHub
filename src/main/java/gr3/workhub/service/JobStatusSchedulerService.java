//package gr3.workhub.service;
//
//import gr3.workhub.entity.Job;
//import gr3.workhub.repository.JobRepository;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class JobStatusSchedulerService {
//
//    private static final Logger logger = LoggerFactory.getLogger(JobStatusSchedulerService.class);
//    private final JobRepository jobRepository;
//
//    private Thread backgroundThread;
//    private volatile boolean running = true;
//
//    @PostConstruct
//    public void startBackgroundThread() {
//        backgroundThread = new Thread(() -> {
//            while (running) {
//                try {
//                    checkAndExpireJobs();
//                    Thread.sleep(5000); // Sleep 5 seconds
//                } catch (InterruptedException e) {
//                    logger.warn("Background thread interrupted, exiting.");
//                    Thread.currentThread().interrupt();
//                    break;
//                } catch (Exception ex) {
//                    logger.error("Error in background thread: ", ex);
//                }
//            }
//        });
//        backgroundThread.setDaemon(true);
//        backgroundThread.start();
//    }
//
//    @PreDestroy
//    public void stopBackgroundThread() {
//        running = false;
//        if (backgroundThread != null) {
//            backgroundThread.interrupt();
//        }
//    }
//
//    private void checkAndExpireJobs() {
//        LocalDateTime now = LocalDateTime.now();
//        List<Job> expiredJobs = jobRepository.findByDeadlineBeforeAndStatus(now, Job.DeadlineStatus.ACTIVE);
//        for (Job job : expiredJobs) {
//            job.setDeadlineStatus(Job.DeadlineStatus.INACTIVE);
//            jobRepository.save(job);
//            logger.info("Set job (id: {}) to INACTIVE due to expired deadline.", job.getId());
//        }
//    }
//}