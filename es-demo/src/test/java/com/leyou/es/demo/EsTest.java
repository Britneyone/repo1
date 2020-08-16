package com.leyou.es.demo;

import com.leyou.es.pojo.Item;
import com.leyou.es.repository.ItemRepository;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsTest {
    @Autowired
    ElasticsearchTemplate template;

    @Autowired
    private ItemRepository itemRepository;



    @Test
    public void testCreate() {
        //创建索引库
        template.createIndex(Item.class);

        //映射关系
        template.putMapping(Item.class);
    }

    @Test
    public void inserttext() {
        List<Item> list = new ArrayList<Item>();
        list.add(new Item(1L, "小米6", "手机", "xiaomi", 2699.00, "abc.jpg"));
        list.add(new Item(2L, "锤子5", "手机", "CHuizi ", 5299.00, "abc.jpg"));
        list.add(new Item(3L, "OPPO Find", "手机", "OPPO", 3329.00, "abc.jpg"));
        list.add(new Item(4L, "荣耀30", "手机", "Rongyao", 3499.00, "abc.jpg"));

        itemRepository.saveAll(list);
    }

    @Test
    public void textFind() {
        Iterable<Item> all = itemRepository.findAll();
        for (Item item : all) {
            System.out.println("item=" + item);
        }
    }


    @Test
    public void textFindBy() {
        List<Item> list = itemRepository.findByPriceBetween(2000d, 4000d);
        for (Item item : list) {
            System.out.println("输出:" + item);
        }
    }

    @Test
    public void testQuery() {
//          查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();


        //结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "title", "price"}, null));
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "小米手机"));


        //排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));

        //分页
        queryBuilder.withPageable(PageRequest.of(0, 2));
        Page<Item> result = itemRepository.search(queryBuilder.build());

        long total = result.getTotalElements();

        System.out.println("total=" + total);
        int totalPages = result.getTotalPages();

        System.out.println("totalPages=" + totalPages);
        List<Item> list = result.getContent();
        for (Item item : list) {
            System.out.println("item=" + item);
        }
    }

    @Test
    public void testAgg() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("popularBrand").field("brand"));
         AggregatedPage<Item> result= template.queryForPage(queryBuilder.build(), Item.class);

           //解析聚合
        Aggregations aggs=result.getAggregations();

        //获取指定集合名词
        StringTerms terms=aggs.get("popularBrand");

        //获取
        List<StringTerms.Bucket> buckets=terms.getBuckets();
        for (StringTerms.Bucket bucket:buckets)
        {
            System.out.println("key +"+bucket.getKeyAsString());
            System.out.println("docCount="+bucket.getDocCount());
        }
    }




}
