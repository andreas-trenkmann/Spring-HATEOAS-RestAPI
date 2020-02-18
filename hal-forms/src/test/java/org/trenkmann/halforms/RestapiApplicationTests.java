package org.trenkmann.halforms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.trenkmann.halforms.config.HypermediaConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import({HypermediaConfiguration.class})
public class RestapiApplicationTests {

  @Test
  public void contextLoads() {
    // proof for clean application start
    Assert.isTrue(true, "always true");
  }

}
