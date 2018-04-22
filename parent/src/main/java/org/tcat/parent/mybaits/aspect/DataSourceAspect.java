//package org.tcat.parent.mybaits.aspect;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//import org.tcat.parent.mybaits.dataSource.DynamicDataSourceHolder;
//
//@Aspect
//@Component
//public class DataSourceAspect {
//
////    @Pointcut("@annotation(org.tcat.parent.mybaits.annotation.EnforceMaster)")
////    public void enforceMaster() {
////    }
////
////    @Before("enforceMaster()")
////    public void beforeEnforceMaster(JoinPoint pjp) throws Throwable {
////        DynamicDataSourceHolder.markMaster();
////    }
//
//    @Pointcut("@annotation(org.tcat.parent.mybaits.annotation.EnforceSlave)")
//    public void enforceSlave() {
//    }
//
//    @Before("enforceSlave()")
//    public void beforeEnforceSlave(JoinPoint pjp) throws Throwable {
//        DynamicDataSourceHolder.markSlave();
//    }
//
//}
