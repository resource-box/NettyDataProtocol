package com.hooniegit.NettyDataProtocol.test;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class Sample implements Serializable {

    private int id;
    private String name;

    @Override
    public String toString() {
        return "Sample{id=" + id + ", name='" + name + "'}";
    }

}
