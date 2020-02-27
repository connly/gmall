package com.project.gmall.search.testBean;

import java.util.List;

public class Movie {
    String id;
    String name;
    String doubanSource;
    List<Actor> actorList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoubanSource() {
        return doubanSource;
    }

    public void setDoubanSource(String doubanSource) {
        this.doubanSource = doubanSource;
    }

    public List<Actor> getActorList() {
        return actorList;
    }

    public void setActorList(List<Actor> actorList) {
        this.actorList = actorList;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", doubanSource='" + doubanSource + '\'' +
                ", actorList=" + actorList +
                '}';
    }
}
