package rddl.competition;
import com.mongodb.*;

import java.util.Date;

public class MongoDB {
    public static void Test()
    {
        MongoClient mongo = new MongoClient( "localhost" , 27017 );
        DB db = mongo.getDB("POMDP_example");
        DBCollection table = db.getCollection("posts");
        BasicDBObject document = new BasicDBObject();
        document.put("name", "mkyong");
        document.put("age", 30);
        document.put("createdDate", new Date());
        table.insert(document);
    }
}
