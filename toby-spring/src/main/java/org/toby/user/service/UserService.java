package org.toby.user.service;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.toby.user.dao.DaoFactory;
import org.toby.user.dao.UserDao;
import org.toby.user.domain.Level;
import org.toby.user.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    UserDao userDao;
    private DataSource dataSource;
    private PlatformTransactionManager transactionManager;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    public UserService(UserDao userDao, DataSource dataSource, PlatformTransactionManager transactionManager) {
        this.userDao = userDao;
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
    }

    public void upgradeLevels() {
         transactionManager =
                new DataSourceTransactionManager(dataSource);
        TransactionStatus status =
                transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();
            for (User user : users) {
                if(canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            transactionManager.commit(status);
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }

    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD: return false;
            default:throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    protected void upgradeLevel(User user) {
        if (user.getLevel() == Level.BASIC) user.setLevel(Level.SILVER);
        else if (user.getLevel() == Level.SILVER) user.setLevel(Level.GOLD);
        userDao.update(user);
        }

    public void add(User user) {
        user.upgradeLevel();
        userDao.add(user);
    }
}
