package com.elexvx.acc.logging;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
  /** 操作摘要 */
  String value();

  /** 操作类型 */
  OperationLogType type() default OperationLogType.OTHER;

  /**
   * 详细描述模版，可使用 {{paramName}} 引用方法参数
   */
  String detail() default "";
}
