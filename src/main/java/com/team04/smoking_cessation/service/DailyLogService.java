package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.DailyLog;
import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.repository.DailyLogRepository;
import com.team04.smoking_cessation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DailyLogService {

    @Autowired
    private DailyLogRepository dailyLogRepository;

    @Autowired
    private UserRepository userRepository;



    public DailyLog createOrUpdateDailyLog(String email, Map<String, Object> logData) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate today = LocalDate.now();
        DailyLog dailyLog = dailyLogRepository.findByUserAndLogDate(user, today)
            .orElse(new DailyLog(user, today));

        // Update log data
        if (logData.containsKey("cigarettesSmoked")) {
            Integer cigarettes = (Integer) logData.get("cigarettesSmoked");
            dailyLog.setCigarettesSmoked(cigarettes);
            dailyLog.setIsSmokeFree(cigarettes == 0);
        }
        if (logData.containsKey("cravingsCount")) {
            dailyLog.setCravingsCount((Integer) logData.get("cravingsCount"));
        }
        if (logData.containsKey("moodLevel")) {
            dailyLog.setMoodLevel((Integer) logData.get("moodLevel"));
        }
        if (logData.containsKey("stressLevel")) {
            dailyLog.setStressLevel((Integer) logData.get("stressLevel"));
        }
        if (logData.containsKey("energyLevel")) {
            dailyLog.setEnergyLevel((Integer) logData.get("energyLevel"));
        }
        if (logData.containsKey("weight")) {
            dailyLog.setWeight((Double) logData.get("weight"));
        }
        if (logData.containsKey("notes")) {
            dailyLog.setNotes((String) logData.get("notes"));
        }

        return dailyLogRepository.save(dailyLog);
    }

    public DailyLog getTodayLog(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return dailyLogRepository.findByUserAndLogDate(user, LocalDate.now())
            .orElse(new DailyLog(user, LocalDate.now()));
    }

    public List<DailyLog> getUserLogHistory(String email, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (startDate != null && endDate != null) {
            return dailyLogRepository.findByUserAndLogDateBetween(user, startDate, endDate);
        } else {
            return dailyLogRepository.findByUserOrderByLogDateDesc(user);
        }
    }

    public Map<String, Object> getUserStatistics(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> stats = new HashMap<>();

        // Get smoke-free days
        Long smokeFreeDays = dailyLogRepository.countSmokeFreeDaysByUser(user);
        stats.put("smokeFreeDays", smokeFreeDays);

        // Calculate money saved (assuming $5 per day)
        Double moneySaved = smokeFreeDays * 5.0;
        stats.put("moneySaved", moneySaved);

        // Get average cigarettes per day for last 30 days
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        Double avgCigarettes = dailyLogRepository.getAverageCigarettesPerDay(user, thirtyDaysAgo, LocalDate.now());
        stats.put("averageCigarettesPerDay", avgCigarettes != null ? avgCigarettes : 0.0);

        // Get total logs count
        List<DailyLog> allLogs = dailyLogRepository.findByUserOrderByLogDateDesc(user);
        stats.put("totalLogsCount", allLogs.size());

        // Advanced analytics
        Map<String, Object> advancedStats = getAdvancedAnalytics(user);
        stats.putAll(advancedStats);

        return stats;
    }

    private Map<String, Object> getAdvancedAnalytics(User user) {
        Map<String, Object> analytics = new HashMap<>();
        List<DailyLog> allLogs = dailyLogRepository.findByUserOrderByLogDateDesc(user);

        if (allLogs.isEmpty()) {
            return analytics;
        }

        // Health metrics trends
        Map<String, Object> healthTrends = calculateHealthTrends(allLogs);
        analytics.put("healthTrends", healthTrends);

        // Craving patterns
        Map<String, Object> cravingPatterns = analyzeCravingPatterns(allLogs);
        analytics.put("cravingPatterns", cravingPatterns);

        // Progress predictions
        Map<String, Object> predictions = generateProgressPredictions(user, allLogs);
        analytics.put("predictions", predictions);

        // Weekly/monthly summaries
        Map<String, Object> summaries = generateSummaries(allLogs);
        analytics.put("summaries", summaries);

        return analytics;
    }

    private Map<String, Object> calculateHealthTrends(List<DailyLog> logs) {
        Map<String, Object> trends = new HashMap<>();

        // Calculate average mood, stress, energy over time
        double avgMood = logs.stream()
            .filter(log -> log.getMoodLevel() != null)
            .mapToInt(DailyLog::getMoodLevel)
            .average()
            .orElse(0.0);

        double avgStress = logs.stream()
            .filter(log -> log.getStressLevel() != null)
            .mapToInt(DailyLog::getStressLevel)
            .average()
            .orElse(0.0);

        double avgEnergy = logs.stream()
            .filter(log -> log.getEnergyLevel() != null)
            .mapToInt(DailyLog::getEnergyLevel)
            .average()
            .orElse(0.0);

        trends.put("averageMood", Math.round(avgMood * 10.0) / 10.0);
        trends.put("averageStress", Math.round(avgStress * 10.0) / 10.0);
        trends.put("averageEnergy", Math.round(avgEnergy * 10.0) / 10.0);

        // Weight trend if available
        List<DailyLog> logsWithWeight = logs.stream()
            .filter(log -> log.getWeight() != null)
            .toList();

        if (logsWithWeight.size() >= 2) {
            double weightChange = logsWithWeight.get(0).getWeight() - logsWithWeight.get(logsWithWeight.size() - 1).getWeight();
            trends.put("weightChange", Math.round(weightChange * 10.0) / 10.0);
        }

        return trends;
    }

    private Map<String, Object> analyzeCravingPatterns(List<DailyLog> logs) {
        Map<String, Object> patterns = new HashMap<>();

        // Average cravings per day
        double avgCravings = logs.stream()
            .filter(log -> log.getCravingsCount() != null)
            .mapToInt(DailyLog::getCravingsCount)
            .average()
            .orElse(0.0);

        patterns.put("averageCravingsPerDay", Math.round(avgCravings * 10.0) / 10.0);

        // Days with highest cravings
        List<DailyLog> highCravingDays = logs.stream()
            .filter(log -> log.getCravingsCount() != null && log.getCravingsCount() > avgCravings * 1.5)
            .toList();

        patterns.put("highCravingDays", highCravingDays.size());

        return patterns;
    }

    private Map<String, Object> generateProgressPredictions(User user, List<DailyLog> logs) {
        Map<String, Object> predictions = new HashMap<>();

        // Predict next milestone based on current progress
        long smokeFreeDays = dailyLogRepository.countSmokeFreeDaysByUser(user);
        
        if (smokeFreeDays < 7) {
            predictions.put("nextMilestone", "7 days smoke-free");
            predictions.put("daysToNextMilestone", 7 - smokeFreeDays);
        } else if (smokeFreeDays < 30) {
            predictions.put("nextMilestone", "30 days smoke-free");
            predictions.put("daysToNextMilestone", 30 - smokeFreeDays);
        } else if (smokeFreeDays < 90) {
            predictions.put("nextMilestone", "90 days smoke-free");
            predictions.put("daysToNextMilestone", 90 - smokeFreeDays);
        } else {
            predictions.put("nextMilestone", "1 year smoke-free");
            predictions.put("daysToNextMilestone", 365 - smokeFreeDays);
        }

        // Predict money saved in next month
        double monthlySavings = smokeFreeDays * 5.0 * 30 / Math.max(smokeFreeDays, 1);
        predictions.put("predictedMonthlySavings", Math.round(monthlySavings));

        return predictions;
    }

    private Map<String, Object> generateSummaries(List<DailyLog> logs) {
        Map<String, Object> summaries = new HashMap<>();

        LocalDate now = LocalDate.now();
        LocalDate weekAgo = now.minusWeeks(1);
        LocalDate monthAgo = now.minusMonths(1);

        // Weekly summary
        long weeklySmokeFreeDays = logs.stream()
            .filter(log -> log.getLogDate().isAfter(weekAgo) && log.getIsSmokeFree())
            .count();
        summaries.put("weeklySmokeFreeDays", weeklySmokeFreeDays);

        // Monthly summary
        long monthlySmokeFreeDays = logs.stream()
            .filter(log -> log.getLogDate().isAfter(monthAgo) && log.getIsSmokeFree())
            .count();
        summaries.put("monthlySmokeFreeDays", monthlySmokeFreeDays);

        // Weekly average cigarettes
        double weeklyAvgCigarettes = logs.stream()
            .filter(log -> log.getLogDate().isAfter(weekAgo) && log.getCigarettesSmoked() != null)
            .mapToInt(DailyLog::getCigarettesSmoked)
            .average()
            .orElse(0.0);
        summaries.put("weeklyAverageCigarettes", Math.round(weeklyAvgCigarettes * 10.0) / 10.0);

        return summaries;
    }

    public Map<String, Object> getCurrentStreak(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<DailyLog> smokeFreeDays = dailyLogRepository.findSmokeFreeDaysByUser(user);

        Map<String, Object> streak = new HashMap<>();

        if (smokeFreeDays.isEmpty()) {
            streak.put("currentStreak", 0);
            streak.put("longestStreak", 0);
            streak.put("lastSmokeDate", null);
            return streak;
        }

        // Calculate current streak
        int currentStreak = 0;
        LocalDate today = LocalDate.now();

        for (DailyLog log : smokeFreeDays) {
            if (log.getLogDate().equals(today.minusDays(currentStreak))) {
                currentStreak++;
            } else {
                break;
            }
        }

        // Calculate longest streak
        int longestStreak = 0;
        int tempStreak = 0;
        LocalDate previousDate = null;

        for (DailyLog log : smokeFreeDays) {
            if (previousDate == null || log.getLogDate().equals(previousDate.minusDays(1))) {
                tempStreak++;
                longestStreak = Math.max(longestStreak, tempStreak);
            } else {
                tempStreak = 1;
            }
            previousDate = log.getLogDate();
        }

        streak.put("currentStreak", currentStreak);
        streak.put("longestStreak", longestStreak);
        streak.put("totalSmokeFreeDays", smokeFreeDays.size());

        return streak;
    }

    public String generateMotivationalMessage(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        DailyLog todayLog = getTodayLog(email);
        
        // Generate motivational message based on user's progress
        StringBuilder message = new StringBuilder();
        
        if (todayLog.getIsSmokeFree()) {
            message.append("🎉 Congratulations! You're smoke-free today. ");
            message.append("Keep up the amazing work! Every smoke-free day is a victory. ");
            message.append("You're making incredible progress on your quit journey. ");
            message.append("Remember: You're stronger than any craving! 💪");
        } else {
            message.append("💪 Don't give up! Every attempt to quit is progress. ");
            message.append("Tomorrow is a new day and a new opportunity. ");
            message.append("You have the power to change your life. ");
            message.append("Keep fighting for your health and future! 🌟");
        }
        
        return message.toString();
    }

    public Map<String, Object> getAIHealthAnalysis(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Return advanced analytics as health analysis
        return getAdvancedAnalytics(user);
    }
}
