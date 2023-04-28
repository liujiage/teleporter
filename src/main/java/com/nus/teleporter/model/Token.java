package com.nus.teleporter.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@Builder
public class Token implements Serializable {

    private String plainText;
    private String uuid;
    private String ts;

}
