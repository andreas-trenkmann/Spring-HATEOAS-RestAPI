package org.trenkmann.restsample.restapi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RestapiApplicationTests {

  @Test
  public void contextLoads() {
    // proof for clean application start
    Assert.isTrue(true, "always true");
  }

}
