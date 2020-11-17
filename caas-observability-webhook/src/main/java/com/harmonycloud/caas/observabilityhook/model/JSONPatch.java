package com.harmonycloud.caas.observabilityhook.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author dengyulong
 * @date 2020/03/25
 */
@Accessors(chain = true)
@Data
public class JSONPatch implements Serializable {

    private static final long serialVersionUID = -7074854185329079458L;

    String op;
    String path;
    Object value;

}
