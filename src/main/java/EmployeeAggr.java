import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmployeeAggr {
    public static void main(String[] args) throws Exception {
        Settings settings=Settings.builder().put("cluster.name","elasticsearch").build();
//        TransportClient client=new PreBuiltTransportClient(settings);
        TransportClient client=new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"),9300));
        employeeAggr(client);
        client.close();
    }

    private static void employeeAggr(TransportClient client) throws Exception {
        SearchResponse response=client.prepareSearch("company")
                .setTypes("employee")
                .addAggregation(
                        AggregationBuilders.terms("group_by_country").field("country")
                        .subAggregation(AggregationBuilders.dateHistogram("group_by_joinDate")
                        .field("join_date")
                                .dateHistogramInterval(DateHistogramInterval.YEAR)
                                .subAggregation(AggregationBuilders.avg("avg_salary").field("salary"))
                        )

                ).execute().actionGet();
        Map<String, Aggregation> aggregationMap = response.getAggregations().asMap();
        Terms terms= (Terms) aggregationMap.get("group_by_country");
        Iterator<? extends Terms.Bucket> bucketIterator = terms.getBuckets().iterator();
        while (bucketIterator.hasNext()){
            Terms.Bucket bucket = bucketIterator.next();
            System.out.println(bucket.getKey()+"....."+bucket.getDocCount());
            Histogram histogram=bucket.getAggregations().get("group_by_joinDate");
            Iterator<? extends Histogram.Bucket> histogramIterator = histogram.getBuckets().iterator();
            while (histogramIterator.hasNext()){
                Histogram.Bucket next = histogramIterator.next();
                System.out.println(next.getKey()+"....."+next.getDocCount());
                Avg avg=next.getAggregations().get("avg_salary");
                System.out.println(avg.getValue());
            }
        }
    }
}
