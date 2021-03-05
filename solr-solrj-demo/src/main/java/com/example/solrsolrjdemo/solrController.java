package com.example.solrsolrjdemo;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/solr")
public class solrController {

    @Value("${spring.data.solr.host}")
    private String URL;

    @GetMapping("solrSelect")
    public String solrSelect(String ids) {
        String result = "";
        try {
            //1.创建连接
            SolrServer solrServer = new HttpSolrServer(URL);
            //2.创建查询语句
            SolrQuery query = new SolrQuery();
            //3.设置查询条件
            query.set("q", "id:" + ids);
            //4.执行查询
            QueryResponse queryResponse = solrServer.query(query);
            //5.取文档列表public class SolrDocumentList extends ArrayList<SolrDocument>
            SolrDocumentList documentList = queryResponse.getResults();
            for (SolrDocument solrDocument : documentList) {
                //取各个文档信息
                System.out.println("item_id:" + solrDocument.get("id") + " ");
                System.out.println("item_title:" + solrDocument.get("item_title") + " ");
                result = solrDocument.get("item_title").toString();
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping("solrSave")
    public void solrSave(String ids, String content) {
        try {
            //1.创建连接对象
            SolrServer solrServer = new HttpSolrServer(URL);
            //2.创建一个文档对象
            SolrInputDocument inputDocument = new SolrInputDocument();
            //向文档中添加域以及对应的值,注意：所有的域必须在schema.xml中定义过,前面已经给出过我定义的域。
            inputDocument.addField("id", ids);
            inputDocument.addField("item_title", content);
            //3.将文档写入索引库中
            solrServer.add(inputDocument);
            //提交
            solrServer.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("solrUpdate")
    public void solrUpdate(String ids, String content) {
        try {
            //1.创建连接对象
            SolrServer solrServer = new HttpSolrServer(URL);
            //2.创建一个文档对象
            SolrInputDocument inputDocument = new SolrInputDocument();
            inputDocument.addField("id", ids);
            //修改id为1的商品的信息（如果该商品不存在其实就是添加了）
            inputDocument.addField("item_title", content);
            //3.将文档写入索引库中
            solrServer.add(inputDocument);
            //提交
            solrServer.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("solrDelete")
    public void solrDeleteById(String ids) {
        try {
            SolrServer solrServer = new HttpSolrServer(URL);
            //删除文档
            solrServer.deleteById(ids);
            //提交
            solrServer.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("solrDeleteQ")
    public void solrDeleteByQ(String content) {
        try {
            SolrServer solrServer = new HttpSolrServer(URL);
            //根据查询结果删除文档，注意：用item_image的查询结果来进行删除是不行的
            //因为制定业务域的时候indexed=false,即不被索引，此时是不能根据图片来查询的。
            solrServer.deleteByQuery("item_title:" + content);
            solrServer.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
