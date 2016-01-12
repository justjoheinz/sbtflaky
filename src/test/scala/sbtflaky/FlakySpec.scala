package sbtflaky

import org.specs2.mutable._

class FlakySpec extends Specification{
  
  "A test" should {
    "be flaky" in {
      true === scala.util.Random.nextBoolean()
    }
  }
   
}