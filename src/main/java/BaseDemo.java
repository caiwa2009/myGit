import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class BaseDemo {
    public static void main(String[] args) throws IOException {
        //如果设置了集群则需要添加集群的名称
        Settings settings=Settings.builder().put("cluster.name","elasticsearch").build();
//        TransportClient client=new PreBuiltTransportClient(settings);
        TransportClient client=new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"),9300));
//        createIndexByKey(client);
//        queryDocument(client);
//        updateDocument(client);
        deleteDocument(client);
//        client.close();
    }
//删除文档
    private static void deleteDocument(TransportClient client) {
        DeleteResponse response=client.prepareDelete("index","type","1").get();

    }

    //    更新文档
    private static void updateDocument(TransportClient client) throws IOException {
        client.prepareUpdate("index","type","1")
                .setDoc(jsonBuilder()
                        .startObject()
                        .field("gender","male")
                        .endObject()
                ).get();
    }


    //    查询文档
    private static void queryDocument(TransportClient client) {
        GetResponse response=client.prepareGet("index","type","1").get();
        System.out.println(response);
    }

    //    创建索引
    private static void createIndexByKey(TransportClient client) throws IOException {
        IndexResponse response=client.prepareIndex("index","type","1")
                .setSource(jsonBuilder()
//                        .setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("user","kimchy")
                        .field("postDate",new Date())
                        .field("message","try out Elasticsearch")
                        .endObject()
                ).get();


    }
}
