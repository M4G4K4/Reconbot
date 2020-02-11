package com.e.reconbot;

public class HistoryItem {

    String results1, results2, results3;
    Long id;
    byte[] photo;

/*    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResults1() {
        return results1;
    }

    public void setResults1(String results1) {
        this.results1 = results1;
    }

    public String getResults2() {
        return results2;
    }

    public void setResults2(String results2) {
        this.results2 = results2;
    }

    public String getResults3() {
        return results3;
    }

    public void setResults3(String results3) {
        this.results3 = results3;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
