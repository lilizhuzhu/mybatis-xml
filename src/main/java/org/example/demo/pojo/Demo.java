package org.example.demo.pojo;

import java.util.List;
import java.util.Date;

/**
 * Auto-generated: 2021-11-13 21:59:17
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Demo {

    private int url;
    private List<Integer> idList;
    private Date gmtTime;
    private People people;
    public void setUrl(int url) {
        this.url = url;
    }
    public int getUrl() {
        return url;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }
    public List<Integer> getIdList() {
        return idList;
    }

    public void setGmtTime(Date gmtTime) {
        this.gmtTime = gmtTime;
    }
    public Date getGmtTime() {
        return gmtTime;
    }

    public void setPeople(People people) {
        this.people = people;
    }
    public People getPeople() {
        return people;
    }

}