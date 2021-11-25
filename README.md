# mybatis-xml
mybatis 字符串sql 解析
使用nacos配置中心做实时动态sql 解析, 

使用mybatis 动态解析 字符串xmlsql 
动态解析if foreach 等标签
```xml
<select>
    select * from school
    <where>
        <if test="id!=null">
            id = #{id}
        </if>
        <if test="name!=null">
            name = #{name}
        </if>
    </where>
</select>
```