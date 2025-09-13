package io.github.quiethappiness.wrench.dynamic.config.center.types.annotations;

import java.lang.annotation.*;

/**
 * 注解，动态配置中心标记
 *
 * @author quiethappiness @jignyue
 * 2025年04月19日09:51:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface DCCValue {

    String value() ;

}