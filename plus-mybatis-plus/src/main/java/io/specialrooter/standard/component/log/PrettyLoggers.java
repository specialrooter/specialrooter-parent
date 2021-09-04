package io.specialrooter.standard.component.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 漂亮的日志（移植版），灵感来源：PrettyBytes
 * 数据源：org.springframework.boot.actuate.logging.LoggersEndpoint
 * @Author Ai
 * @Version v1.0
 * @Date 2020-04-21 11:12:12
 */
@Component
public class PrettyLoggers {
    @Autowired
    private LoggingSystem loggingSystem;
    @Autowired
    private LoggerGroups loggerGroups;

    /**
     * 所有日志
     *
     * @return
     */
    public Map<String, Object> loggers() {
        Collection<LoggerConfiguration> configurations = this.loggingSystem.getLoggerConfigurations();
        if (configurations == null) {
            return Collections.emptyMap();
        } else {
            Map<String, Object> result = new LinkedHashMap();
            result.put("levels", this.getLevels());
            result.put("loggers", this.getLoggers(configurations));
            result.put("groups", this.getGroups());
            return result;
        }
    }

    /**
     * 所有日志分组
     *
     * @return
     */
    private Map<String, LoggerLevels> getGroups() {
        Map<String, LoggerLevels> groups = new LinkedHashMap();
        this.loggerGroups.forEach((group) -> {
            LoggerLevels var10000 =  groups.put(group.getName(), new GroupLoggerLevels(group.getConfiguredLevel(), group.getMembers()));
        });
        return groups;
    }

    /**
     * 获取单个类的日志等级
     *
     * @param name
     * @return
     */
    public LoggerLevels loggerLevels(String name) {
        Assert.notNull(name, "Name must not be null");
        LoggerGroup group = this.loggerGroups.get(name);
        if (group != null) {
            return new GroupLoggerLevels(group.getConfiguredLevel(), group.getMembers());
        } else {
            LoggerConfiguration configuration = this.loggingSystem.getLoggerConfiguration(name);
            return configuration != null ? new SingleLoggerLevels(configuration) : null;
        }
    }

    /**
     * 设置日志等级
     *
     * @param name
     * @param configuredLevel
     */
    public void configureLogLevel(String name, @Nullable LogLevel configuredLevel) {
        Assert.notNull(name, "Name must not be empty");
        LoggerGroup group = this.loggerGroups.get(name);
        if (group != null && group.hasMembers()) {
            LoggingSystem var10002 = this.loggingSystem;
            group.configureLogLevel(configuredLevel, var10002::setLogLevel);
        } else {
            this.loggingSystem.setLogLevel(name, configuredLevel);
        }
    }

    private NavigableSet<LogLevel> getLevels() {
        Set<LogLevel> levels = this.loggingSystem.getSupportedLogLevels();
        return (new TreeSet(levels)).descendingSet();
    }

    private Map<String, LoggerLevels> getLoggers(Collection<LoggerConfiguration> configurations) {
        Map<String, LoggerLevels> loggers = new LinkedHashMap(configurations.size());
        Iterator var3 = configurations.iterator();

        while (var3.hasNext()) {
            LoggerConfiguration configuration = (LoggerConfiguration) var3.next();
            loggers.put(configuration.getName(), new SingleLoggerLevels(configuration));
        }

        return loggers;
    }

    public static class SingleLoggerLevels extends LoggerLevels {
        private String effectiveLevel;

        public SingleLoggerLevels(LoggerConfiguration configuration) {
            super(configuration.getConfiguredLevel());
            this.effectiveLevel = this.getName(configuration.getEffectiveLevel());
        }

        public String getEffectiveLevel() {
            return this.effectiveLevel;
        }
    }

    public static class GroupLoggerLevels extends LoggerLevels {
        private List<String> members;

        public GroupLoggerLevels(LogLevel configuredLevel, List<String> members) {
            super(configuredLevel);
            this.members = members;
        }

        public List<String> getMembers() {
            return this.members;
        }
    }

    public static class LoggerLevels {
        private String configuredLevel;

        public LoggerLevels(LogLevel configuredLevel) {
            this.configuredLevel = this.getName(configuredLevel);
        }

        protected final String getName(LogLevel level) {
            return level != null ? level.name() : null;
        }

        public String getConfiguredLevel() {
            return this.configuredLevel;
        }
    }
}
