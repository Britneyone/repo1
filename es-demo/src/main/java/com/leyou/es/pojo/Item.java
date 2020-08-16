package com.leyou.es.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.lang.reflect.Type;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "heima3",type = "item",shards = 1,replicas = 1 )
public class Item {

    @Field(type = FieldType.Long)
    @Id
    Long id;

    //标题
    @Field(type=FieldType.Text,analyzer = "iK_smart")
    String title;

    @Field(type = FieldType.Keyword)
    String Category;

    @Field(type =FieldType.Keyword)
    String brand;

    @Field(type = FieldType.Double)
    Double price;

    @Field(type =FieldType.Keyword)
    String images;

}
