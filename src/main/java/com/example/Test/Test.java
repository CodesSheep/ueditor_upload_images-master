package com.example.Test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Test implements Serializable {

    private static final long serialVersionUID = -4853048277449091549L;
    private String content;
}
