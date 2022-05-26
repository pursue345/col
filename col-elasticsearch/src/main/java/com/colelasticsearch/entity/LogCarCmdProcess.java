package com.colelasticsearch.entity;


import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@Document(indexName = "/av234")
public class LogCarCmdProcess implements Serializable {

    private String cmdLogId;
}
