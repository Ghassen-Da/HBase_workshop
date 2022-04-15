package tn.insat.tp4;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

public class HbaseSparkProcess {

    public void createHbaseTable() {
        Configuration config = HBaseConfiguration.create();
        SparkConf sparkConf = new SparkConf().setAppName("SparkHBaseTest").setMaster("local[4]");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        config.set(TableInputFormat.INPUT_TABLE,"products");

        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD =
                jsc.newAPIHadoopRDD(config, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);

        JavaRDD<String> javaRDD = hBaseRDD.map((Function<Tuple2<ImmutableBytesWritable, Result>, String>) tuple->{
        	Result result = tuple._2;
        	return Bytes.toString(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("price")));
        });
        
        String sumResult = javaRDD.reduce((value1, value2)-> String.valueOf(Float.parseFloat(value1)+Float.parseFloat(value2)));
        
        System.out.println("The Sum of products is: " + sumResult);
    }

    public static void main(String[] args){
        HbaseSparkProcess admin = new HbaseSparkProcess();
        admin.createHbaseTable();
    }

}
