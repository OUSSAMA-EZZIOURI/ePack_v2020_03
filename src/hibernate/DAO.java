/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate;

import helper.Helper;
import javax.persistence.Table;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;

/**
 *
 * @author user
 */
public class DAO {

    public int create(Object obj) {

        try {
            Helper.sess.beginTransaction();
            int id = (int) Helper.sess.save(obj);
            Helper.sess.getTransaction().commit();
            //Helper.sess.flush();
            //Helper.sess.clear();
            return id;
        } catch (HibernateException e) {
            if (Helper.sess.getTransaction() != null && Helper.sess.getTransaction().isActive()) {
                try {
                    // Second try catch as the rollback could fail as well
                    Helper.sess.getTransaction().rollback();
                } catch (HibernateException e1) {
                    System.out.println("Error rolling back transaction");
                    System.out.println(e1.getMessage());
                }
                // throw again the first exception
                System.out.println(e.getMessage());
                throw e;
            }
        }
        return 0;
    }

    public void update(Object obj) {
        try {

            Helper.sess.beginTransaction();
            Helper.sess.update(obj);

            Helper.sess.getTransaction().commit();
            Helper.sess.flush();
            Helper.sess.clear();
        } catch (HibernateException e) {
            if (Helper.sess.getTransaction() != null && Helper.sess.getTransaction().isActive()) {
                try {
                    // Second try catch as the rollback could fail as well
                    Helper.sess.getTransaction().rollback();
                } catch (HibernateException e1) {
                    System.out.println("Error rolling back transaction");
                    System.out.println(e1.getMessage());
                }
                // throw again the first exception
                System.out.println(e.getMessage());
                throw e;
            }
        }
    }

    public void delete(Object obj) {
        try {
            Helper.sess.beginTransaction();
            Helper.sess.delete(obj);
            Helper.sess.getTransaction().commit();
            Helper.sess.flush();
            Helper.sess.clear();
        } catch (HibernateException e) {
            if (Helper.sess.getTransaction() != null && Helper.sess.getTransaction().isActive()) {
                try {
                    // Second try catch as the rollback could fail as well
                    Helper.sess.getTransaction().rollback();
                } catch (HibernateException e1) {
                    System.out.println("Error rolling back transaction");
                    System.out.println(e1.getMessage());
                }
                // throw again the first exception
                System.out.println(e.getMessage());
                throw e;
            }
        }
    }

    public int trancate(Object obj) {
        try {
            Class<?> c = obj.getClass();
            Table table = c.getAnnotation(Table.class);
            String tableName = table.name();
            System.out.println("HOla "+tableName);
            Helper.sess.beginTransaction();
            String sql = String.format("TRUNCATE TABLE %s", tableName);
            System.out.println("SQL "+sql);
            SQLQuery query = Helper.sess.createSQLQuery(sql);
            int i = query.executeUpdate();
            Helper.sess.getTransaction().commit();
            return i;

        } catch (HibernateException e) {
            if (Helper.sess.getTransaction() != null && Helper.sess.getTransaction().isActive()) {
                try {
                    // Second try catch as the rollback could fail as well
                    Helper.sess.getTransaction().rollback();
                } catch (HibernateException e1) {
                    System.out.println("Error rolling back transaction");
                    System.out.println(e1.getMessage());
                }
                // throw again the first exception
                System.out.println(e.getMessage());
                throw e;
            }
        }
        return 0;
    }

}
