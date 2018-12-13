/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package core.xmate.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    String name();

    String[] onCreated() default "";

    boolean virtual() default false;

    /*Full text search*/

    String FTS3 = "fts3";
    /**
     * fts3的改进版本，API 11之后可用
     */
    String FTS4 = "fts4";
    /**
     * 考虑到兼容性，不建议使用fts5
     */
    String FTS5 = "fts5";

    @Retention(RetentionPolicy.SOURCE)
    @interface FTS {
    }

    /**
     * 当该属性启用时所创建的表默认为virtual表。
     * 可选的属性请参见{@link FTS#}。
     *
     * @return
     */
    @FTS
    String using() default "";

    /**
     * 默认的tokenizer
     */
    String TOKENIZER_SIMPLE = "simple";
    String TOKENIZER_PORTER = "porter";
    /**
     * 支持中文，但只是按照逗号分隔而已。
     */
    String TOKENIZER_UNICODE61 = "unicode61";
    /**
     * 支持中文，但有可能导致无法建表
     */
    String TOKENIZER_ICU = "icu";

    @Retention(RetentionPolicy.SOURCE)
    @interface Tokenizer {
    }

    /**
     * 当启用{@link #using()}属性时用于fts3/4的tokenizer，默认为{@link #TOKENIZER_SIMPLE}。
     * 可用属性请参加{@link Tokenizer#}
     *
     * @return
     */
    @Tokenizer
    String tokenizer() default TOKENIZER_SIMPLE;
}