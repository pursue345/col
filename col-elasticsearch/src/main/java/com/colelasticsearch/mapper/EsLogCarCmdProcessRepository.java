package com.colelasticsearch.mapper;

import com.colelasticsearch.entity.LogCarCmdProcess;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsLogCarCmdProcessRepository extends ElasticsearchRepository<LogCarCmdProcess,Long> {



}