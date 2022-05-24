package com.cqx.common.utils.jdbc;

import com.cqx.common.annotation.DB_StrToClob;

import java.sql.Date;

/**
 * <pre>
 *     select * from cqx_test5;
 *     --update cqx_test5 set id=456;
 *     create table cqx_test5(
 *     id number(10),
 *     name varchar2(200),
 *     time date,
 *     description clob
 *     );
 *     insert into cqx_test5 values(123,'test1',sysdate,EMPTY_CLOB());
 * </pre>
 *
 * @author chenqixu
 */
public class CqxTest5Bean {
    private long id;
    private String name;
    private Date time;
//    @DB_StrToClob
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
