package org.tron.core.db;

import java.io.File;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.tron.common.utils.FileUtil;
import org.tron.core.Constant;
import org.tron.core.capsule.TransactionResultCapsule;
import org.tron.core.config.DefaultConfig;
import org.tron.core.config.args.Args;
import org.tron.core.exception.BadItemException;
import org.tron.protos.Protocol.Transaction.Result.code;

public class TransactionHistoryTest {

  private static String dbPath = "output_TransactionHistoryStore_test";
  private static String dbDirectory = "db_TransactionHistoryStore_test";
  private static String indexDirectory = "index_TransactionHistoryStore_test";
  private static AnnotationConfigApplicationContext context;
  private static TransactionHistoryStore transactionHistoryStore;
  private static final byte[] transactionId = TransactionStoreTest.randomBytes(32);

  static {
    Args.setParam(
        new String[]{
            "--output-directory", dbPath,
            "--storage-db-directory", dbDirectory,
            "--storage-index-directory", indexDirectory
        },
        Constant.TEST_CONF
    );
    context = new AnnotationConfigApplicationContext(DefaultConfig.class);
  }

  @AfterClass
  public static void destroy() {
    Args.clearParam();
    FileUtil.deleteDir(new File(dbPath));
    context.destroy();
  }

  @BeforeClass
  public static void init() {
    transactionHistoryStore = context.getBean(TransactionHistoryStore.class);
    TransactionResultCapsule transactionResultCapsule = new TransactionResultCapsule(code.SUCESS,
        1000L);

    transactionResultCapsule.setBlockNumber(100L);
    transactionResultCapsule.setBlockTimeStamp(200L);
    transactionHistoryStore.put(transactionId, transactionResultCapsule);
  }

  @Test
  public void get() throws BadItemException {
    //test get and has Method
    TransactionResultCapsule resultCapsule = transactionHistoryStore.get(transactionId);
    Assert.assertEquals(1000L, resultCapsule.getFee());
    Assert.assertEquals(100L, resultCapsule.getBlockNumber());
    Assert.assertEquals(200L, resultCapsule.getBlockTimeStamp());
  }
}