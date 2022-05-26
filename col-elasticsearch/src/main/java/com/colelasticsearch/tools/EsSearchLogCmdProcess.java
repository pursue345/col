package com.colelasticsearch.tools;

import com.colelasticsearch.mapper.EsLogCarCmdProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

public class EsSearchLogCmdProcess {
    private static final Logger logger = LoggerFactory.getLogger(EsSearchLogCmdProcess.class);

    /**

     * ES持久化操作类

     */

    private final EsLogCarCmdProcessRepository esGoodsDetailRepository;

    /**

     * ES通用Rest操作客户端

     */

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**

     * 商品明细管理Service

     */

    private final IGoodsDetailService goodsDetailService;

    /**

     * 商品策略管理Service

     */

    private final IGoodsStrategyService goodsStrategyService;

    /**

     * 商品资源管理Service

     */

    private final IGoodsResourceService goodsResourceService;

    /**

     * 通用Kafka消息发送-生产者

     */

    private final KafkaCommonProducer kafkaCommonProducer;


    public EsSearchLogCmdProcess(EsLogCarCmdProcessRepository esGoodsDetailRepository) {
        this.esGoodsDetailRepository = esGoodsDetailRepository;
    }

    public PageResponse<GoodsVO> searchGoodsInfo(GoodsSearchRequest goodsSearchRequest, PageRequest pageRequest) {

        PageResponse<GoodsVO> pageResponse;

        // 解析查询参数并路由到对应的查询功能

        GoodsSearchConditionEnum searchConditionEnum = parseAndRouteQuery(goodsSearchRequest);

        switch (searchConditionEnum) {

            case GOODS_SEARCH_BY_ID:

                pageResponse = queryGoodsByIdCondition(goodsSearchRequest.getCode());

                break;

            case GOODS_SEARCH_BY_NAME:

                pageResponse = queryGoodsByNameCondition(goodsSearchRequest.getName());

                break;

            default:

                pageResponse = queryGoodsByGenericCondition(goodsSearchRequest, pageRequest);

                break;

        }

        return pageResponse;

    }

    public EsGoodsDetail saveGoodsInfo(EsGoodsDetail goodsDetail) {

        return esGoodsDetailRepository.save(goodsDetail);

    }

    public void deleteGoodsInfoById(Long id) {

        esGoodsDetailRepository.deleteById(id);

    }

    public EsGoodsDetail findGoodsInfoById(Long id) {

        Optional<EsGoodsDetail> optional = esGoodsDetailRepository.findById(id);

        return optional.orElse(null);

    }

    public void createIndex(String esDocEntityName, Boolean isOverride) {

        if (StrUtil.isBlank(esDocEntityName)) {

            logger.warn("目前只能创建指定名称的文档索引:{},{}", esDocEntityName, isOverride);

            return;

        }

        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(EsGoodsDetail.class);

        // 删除现有索引

        indexOperations.delete();

        // 创建新的索引

        indexOperations.create();

        indexOperations.refresh();

        indexOperations.putMapping(indexOperations.createMapping());

    }

    public void loadGoodsInfoById(Long goodsId) {

        EsGoodsDetail esGoodsDetail = new EsGoodsDetail();

        // Step1: 查询商品基本信息并转换

        GoodsDetail goodsDetail = goodsDetailService.getById(goodsId);

        if (goodsDetail == null) {

            LoggerUtil.warn(logger, "指定商品不存在:{}", goodsId);

            return;

        }

        BeanUtil.copyProperties(goodsDetail, esGoodsDetail);

        // Step2: 查询商品资源信息并转换

        QueryWrapper<GoodsResource> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("goods_id", goodsId);

        List<GoodsResource> goodsResources = goodsResourceService.getBaseMapper().selectList(queryWrapper);

        goodsResources.forEach(goodsResource -> {

            if (GoodsResourceTypeEnum.RICH_TEXT_DETAIL.equals(goodsResource.getType())) {

                esGoodsDetail.setGoodResourceOfDescText(goodsResource.getContent());

            }

        });

        // Step3: 查询商品策略信息并转换

        GoodsStrategyQueryRequest goodsStrategyQueryRequest = new GoodsStrategyQueryRequest();

        goodsStrategyQueryRequest.setObjectType(StrategyObjectTypeEnum.GOODS);

        goodsStrategyQueryRequest.setObjectId(goodsId);

        GoodsStrategyVO goodsStrategyVO = goodsStrategyService.queryGoodsStrategy(goodsStrategyQueryRequest);

        if (goodsStrategyVO != null) {

            formatGoodsStrategyData(esGoodsDetail, goodsStrategyVO);

        }

        // Step4: 重新索引数据到ES

        saveGoodsInfo(esGoodsDetail);

    }

    public void reIndexAllGoodsInfo() {

        List<GoodsDetail> goodsDetails = goodsDetailService.list();

        // 数据量大的情况下应该要考虑到分页和多线程处理

        List<Long> goodIds = goodsDetails.stream().map(GoodsDetail::getId).collect(Collectors.toList());

        goodIds.forEach(goodsId -> {

            deleteGoodsInfoById(goodsId);

            loadGoodsInfoById(goodsId);

        });

    }

    public void sendKafkaMessage(GoodsChangeDTO goodsChangeDTO) {

        logger.info("发送商品变更Kafka消息:{}", JSON.toJSONString(goodsChangeDTO));

        kafkaCommonProducer.sendMessage(GoodsSearchConst.GOODS_CHANGE_KAFKA_TOPIC, JSON.toJSONString(goodsChangeDTO));

    }

    /**

     * 格式化商品策略部分的信息到EsGoodsDetail

     *

     * @param esGoodsDetail   ES索引文档

     * @param goodsStrategyVO 商品策略VO

     */

    private void formatGoodsStrategyData(EsGoodsDetail esGoodsDetail, GoodsStrategyVO goodsStrategyVO) {

        List<EsGoodsStrategy> esGoodsStrategyList = new ArrayList<>();



        // Step1: 商品策略-->上架时间处理

        if (goodsStrategyVO.getOnOffLineTimeStrategyVO() != null) {

            GoodsStrategyVO.OnOffLineTimeStrategyVO shelfTimeVO = goodsStrategyVO.getOnOffLineTimeStrategyVO();

            timeStrategy(goodsStrategyVO, esGoodsStrategyList, shelfTimeVO.getStartTime(), shelfTimeVO.getEndTime());

        }



        // Step2: 商品策略-->商品时间处理

        if (goodsStrategyVO.getSalesTimeStrategyVO() != null) {

            GoodsStrategyVO.SalesTimeStrategyVO salesTimeStrategyVO = goodsStrategyVO.getSalesTimeStrategyVO();

            timeStrategy(goodsStrategyVO, esGoodsStrategyList, salesTimeStrategyVO.getStartTime(), salesTimeStrategyVO.getEndTime());

        }

        // Step3: 商品策略-->车系编码处理

        if (goodsStrategyVO.getCarSeriesStrategyVO() != null) {

            GoodsStrategyVO.CarSeriesStrategyVO carSeriesStrategyVO = goodsStrategyVO.getCarSeriesStrategyVO();

            EsGoodsStrategy carSeriesStrategy = new EsGoodsStrategy();

            // 基础属性设置

            BeanUtils.copyProperties(goodsStrategyVO, carSeriesStrategy);

            // 车系编码设置

            carSeriesStrategy.setCarSeries(carSeriesStrategyVO.getCarSeries());

            esGoodsStrategyList.add(carSeriesStrategy);

        }

        // Step4: 商品策略-->车型编码处理

        if (goodsStrategyVO.getCarModelStrategyVO() != null) {

            GoodsStrategyVO.CarModelStrategyVO carModelStrategyVO = goodsStrategyVO.getCarModelStrategyVO();

            EsGoodsStrategy carModesStrategy = new EsGoodsStrategy();

            // 基础属性设置

            BeanUtils.copyProperties(goodsStrategyVO, carModesStrategy);

            // 车型编码设置

            carModesStrategy.setCarModes(carModelStrategyVO.getCarModels());

            esGoodsStrategyList.add(carModesStrategy);

        }

        // Step5: 商品策略-->车辆编码处理

        if (goodsStrategyVO.getCarVinStrategyVO() != null) {

            GoodsStrategyVO.CarVinStrategyVO carVinStrategyVO = goodsStrategyVO.getCarVinStrategyVO();

            EsGoodsStrategy carVinCodesStrategy = new EsGoodsStrategy();

            // 基础属性设置

            BeanUtils.copyProperties(goodsStrategyVO, carVinCodesStrategy);

            // 车辆VIN编码设置

            carVinCodesStrategy.setCarVinCodes(carVinStrategyVO.getCarVinCodes());

            esGoodsStrategyList.add(carVinCodesStrategy);

        }



        esGoodsDetail.setEsGoodsStrategyList(esGoodsStrategyList);

    }



    private void timeStrategy(GoodsStrategyVO goodsStrategyVO, List<EsGoodsStrategy> esGoodsStrategyList, LocalTime startTime,

                              LocalTime endTime) {

        EsGoodsStrategy shelfTimeStrategy = new EsGoodsStrategy();

        // 基础属性设置

        BeanUtils.copyProperties(goodsStrategyVO, shelfTimeStrategy);



        Map<String, Long> shelfTimes = new HashMap<>(2);

        // 商品策略：商品上架起始时间，当天时间的秒数

        shelfTimes.putIfAbsent("gte", startTime.getLong(ChronoField.SECOND_OF_DAY));

        // 商品策略：商品上架结束时间，当天时间的秒数

        shelfTimes.putIfAbsent("lte", endTime.getLong(ChronoField.SECOND_OF_DAY));

        shelfTimeStrategy.setStrategyOfShelfTimes(shelfTimes);



        esGoodsStrategyList.add(shelfTimeStrategy);

    }

    /**

     * 查询Condition::根据商品ID查询商品详情

     *

     * @param goodsId 商品ID

     * @return GoodsDetailVo

     */

    PageResponse<GoodsVO> queryGoodsByIdCondition(String goodsId) {

        return new PageResponse<>();

    }

    /**

     * 查询Condition::根据商品名称查询商品详情

     *

     * @param name 商品名称

     * @return pageResponse

     */

    PageResponse<GoodsVO> queryGoodsByNameCondition(String name) {

        return new PageResponse<>();

    }

    /**

     * 查询Condition：通用查询

     *

     * @param goodsSearchRequest 查询参数

     * @param pageRequest        分页参数

     * @return PageResponse

     */

    PageResponse<GoodsVO> queryGoodsByGenericCondition(GoodsSearchRequest goodsSearchRequest, PageRequest pageRequest) {

        // Step1: 查询条件设置和准备

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        prepareGenericQueryParam(boolQueryBuilder, goodsSearchRequest);

        // Step2: 分页查询参数准备

        int curPage = (int) pageRequest.getCurrentPage() - 1;

        int pageSize = (int) pageRequest.getPageSize();

        org.springframework.data.domain.PageRequest curPageParam = org.springframework.data.domain.PageRequest.of(curPage, pageSize);

        // FIXME Step3: 排序参数处理和设置 searchQuery.withSort()



        // Step4: 查询请求构建和组装

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()

                .withQuery(boolQueryBuilder)

                .withPageable(curPageParam)

                .build();

        // Step5: 执行查询

        SearchHits<EsGoodsDetail> searchHits = elasticsearchRestTemplate.search(searchQuery, EsGoodsDetail.class);

        // Step6: 最终结果包装和后处理

        return wrapperGoodsInfo(searchHits, pageRequest);

    }

    /**

     * 对ES查询的结果数据进行转换和替换

     *

     * @param searchHits ES搜索结果列表

     * @return PageResponse

     */

    private PageResponse<GoodsVO> wrapperGoodsInfo(SearchHits<EsGoodsDetail> searchHits, PageRequest pageRequest) {

        List<SearchHit<EsGoodsDetail>> searchHitList = searchHits.getSearchHits();

        List<GoodsVO> goodsVOList = new ArrayList<>(searchHitList.size());

        searchHitList.forEach(esGoodsDetailSearchHit -> {

            EsGoodsDetail esGoodsDetail = esGoodsDetailSearchHit.getContent();

            GoodsVO goodsVO = format2GoodsVO(esGoodsDetail);

            goodsVOList.add(goodsVO);

        });

        // 分页参数设置

        Pagination pagination = new Pagination();

        pagination.setTotal(searchHits.getTotalHits());

        pagination.setCurrentPage(pageRequest.getCurrentPage());

        pagination.setPageSize(pageRequest.getPageSize());

        pagination.setTotalPage(searchHits.getTotalHits() / pageRequest.getPageSize() + 1);

        // 数据列表设置

        return new PageResponse<>(goodsVOList, pagination);

    }

    /**

     * 格式化ES商品数据到前端VO进行展示

     *

     * @param esGoodsDetail ES商品详情

     * @return GoodsVO

     */

    private GoodsVO format2GoodsVO(EsGoodsDetail esGoodsDetail) {

        GoodsVO goodsVO = new GoodsVO();

        BeanUtils.copyProperties(esGoodsDetail, goodsVO);

        // 商品详情

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();

        BeanUtils.copyProperties(esGoodsDetail, goodsDetailVo);

        if (esGoodsDetail.getCurrentPrice() != null) {

            goodsDetailVo.setCurrentPrice(new BigDecimal(esGoodsDetail.getCurrentPrice()));

        }

        if (esGoodsDetail.getOriginalPrice() != null) {

            goodsDetailVo.setOriginalPrice(new BigDecimal(esGoodsDetail.getOriginalPrice()));

        }

        goodsVO.setGoodsDetailVo(goodsDetailVo);

        // 商品策略

        // 商品SKU列表

        return goodsVO;

    }

    /**

     * 准备通用查询参数

     *

     * @param boolQueryBuilder Bool查询过滤器

     */

    private void prepareGenericQueryParam(BoolQueryBuilder boolQueryBuilder, GoodsSearchRequest goodsSearchRequest) {

        //Step1: ID查询参数处理->商品ID精确查询

        if (goodsSearchRequest.getGoodsId() != null) {

            MatchQueryBuilder nameMatchQueryBuilder = QueryBuilders.matchQuery("id", goodsSearchRequest.getGoodsId());

            boolQueryBuilder.must(nameMatchQueryBuilder);

        }

        //Step1: name查询参数处理->分词并匹配商品名称

        if (StrUtil.isNotBlank(goodsSearchRequest.getName())) {

            MatchQueryBuilder nameMatchQueryBuilder = QueryBuilders.matchQuery("name", goodsSearchRequest.getName());

            boolQueryBuilder.must(nameMatchQueryBuilder);

        }

        //Step2: keywords查询参数处理->不分词，并在name和商品详情字段搜索

        if (StrUtil.isNotBlank(goodsSearchRequest.getKeyWords())) {

            MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders

                    .multiMatchQuery(goodsSearchRequest.getKeyWords(), "name", "goodResourceOfDescText");

            boolQueryBuilder.must(matchQueryBuilder);

        }

        //Step3: payChannelIds查询参数处理->商品支付渠道查询

        if (!CollUtil.isEmpty(goodsSearchRequest.getPayChannelIds())) {

            goodsSearchRequest.getPayChannelIds().forEach(payChannelId -> {

                TermQueryBuilder payChannelTermQueryBuilder = QueryBuilders.termQuery("payChannelIds", payChannelId);

                boolQueryBuilder.must(payChannelTermQueryBuilder);

            });

        }



        //Step4: 其他通用查询参数-单字段

        prepareGenericQueryParamBySingleProperty(boolQueryBuilder, goodsSearchRequest);



        //Step5: 其他通用查询参数-List字段

        prepareGenericQueryParamByListProperty(boolQueryBuilder, goodsSearchRequest);



        //Step6: esGoodsStrategyList查询参数处理->商品策略查询参数处理

        prepareGenericQueryParamByGoodsStrategy(boolQueryBuilder, goodsSearchRequest);



        //Step7: esGoodsSkuList查询参数处理->商品SKU信息查询参数处理

        prepareGenericQueryParamByGoodsSkuInfo(boolQueryBuilder, goodsSearchRequest);

    }

    /**

     * esGoodsSkuList查询参数处理->商品SKU信息查询参数处理

     *

     * @param boolQueryBuilder   BOOL查询过滤器

     * @param goodsSearchRequest 查询请求

     */

    private void prepareGenericQueryParamByGoodsSkuInfo(BoolQueryBuilder boolQueryBuilder, GoodsSearchRequest goodsSearchRequest) {

    }

    /**

     * esGoodsStrategyList查询参数处理->商品策略查询参数处理

     *

     * @param boolQueryBuilder   BOOL查询过滤器

     * @param goodsSearchRequest 查询请求

     */

    private void prepareGenericQueryParamByGoodsStrategy(BoolQueryBuilder boolQueryBuilder, GoodsSearchRequest goodsSearchRequest) {

        //Step1：商品策略：上下架时间段处理

        if (goodsSearchRequest.getStrategyOfShelfTime() != null) {

            LocalTime shelfTime = goodsSearchRequest.getStrategyOfShelfTime();

            Long shelfTimeLong = shelfTime.getLong(ChronoField.SECOND_OF_DAY);

            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("esGoodsStrategyList.strategyOfShelfTimes", shelfTimeLong);

            NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("esGoodsStrategyList", termQueryBuilder, ScoreMode.None);

            boolQueryBuilder.must(nestedQueryBuilder);

        }

        //Step2：商品策略：可销售时间段处理,同上

        //Step3：商品策略：车系编码处理--目前只针对单个数据

        if (StrUtil.isNotBlank(goodsSearchRequest.getStrategyOfCarSeries())) {

            TermQueryBuilder termQueryBuilder = QueryBuilders

                    .termQuery("esGoodsStrategyList.carSeries", goodsSearchRequest.getStrategyOfCarSeries());

            NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("esGoodsStrategyList", termQueryBuilder, ScoreMode.None);

            boolQueryBuilder.must(nestedQueryBuilder);

        }

        //Step4：商品策略：车型编码处理--目前只针对单个数据

        if (StrUtil.isNotBlank(goodsSearchRequest.getStrategyOfCarModes())) {

            TermQueryBuilder termQueryBuilder = QueryBuilders

                    .termQuery("esGoodsStrategyList.carModes", goodsSearchRequest.getStrategyOfCarModes());

            NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("esGoodsStrategyList", termQueryBuilder, ScoreMode.None);

            boolQueryBuilder.must(nestedQueryBuilder);

        }

        //Step5：商品策略：车辆VIN码处理--目前只针对单个数据

        if (StrUtil.isNotBlank(goodsSearchRequest.getStrategyOfCarVinCodes())) {

            TermQueryBuilder termQueryBuilder = QueryBuilders

                    .termQuery("esGoodsStrategyList.carVinCodes", goodsSearchRequest.getStrategyOfCarVinCodes());

            NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("esGoodsStrategyList", termQueryBuilder, ScoreMode.None);

            boolQueryBuilder.must(nestedQueryBuilder);

        }

    }

    /**

     * 其他通用查询参数-List字段

     *

     * @param boolQueryBuilder   BOOL查询过滤器

     * @param goodsSearchRequest 查询请求

     */

    private void prepareGenericQueryParamByListProperty(BoolQueryBuilder boolQueryBuilder, GoodsSearchRequest goodsSearchRequest) {

    }

    /**

     * 其他通用查询参数-单字段

     *

     * @param boolQueryBuilder   BOOL查询过滤器

     * @param goodsSearchRequest 查询请求

     */

    private void prepareGenericQueryParamBySingleProperty(BoolQueryBuilder boolQueryBuilder, GoodsSearchRequest goodsSearchRequest) {

        //Step1：商品价格查询处理currentPrice

        Integer priceMin = goodsSearchRequest.getCurrentPriceMin();

        Integer priceMax = goodsSearchRequest.getCurrentPriceMax();

        if (priceMin != null && priceMax != null) {

            RangeQueryBuilder priceRangeQueryBuilder = QueryBuilders.rangeQuery("currentPrice").gte(priceMin).lte(priceMax);

            boolQueryBuilder.must(priceRangeQueryBuilder);

        }

    }

    /**

     * 查询场景细分处理

     *

     * @param goodsSearchRequest 商品查询参数

     * @return GoodsSearchConditionEnum

     */

    private GoodsSearchConditionEnum parseAndRouteQuery(GoodsSearchRequest goodsSearchRequest) {

        return GoodsSearchConditionEnum.GOODS_SEARCH_GENERIC;

    }

}
