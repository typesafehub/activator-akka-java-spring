package sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import static akka.pattern.Patterns.ask;
import akka.util.Timeout;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import sample.CountingActor.Count;
import sample.CountingActor.Get;
import static sample.SpringExtension.SpringExtProvider;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

public class SpringTest {
  @Test
  public void testSpring() throws Exception {
    // create a spring context and scan the classes
    AnnotationConfigApplicationContext ctx =
      new AnnotationConfigApplicationContext();
    ctx.scan("sample");
    ctx.refresh();

    // get hold of the actor system
    ActorSystem system = ctx.getBean(ActorSystem.class);
    // use the Spring Extension to create props for a named actor bean
    ActorRef counter = system.actorOf(
      SpringExtProvider.get(system).props("CountingActor"), "counter");

    // tell it to count three times
    counter.tell(new Count(), null);
    counter.tell(new Count(), null);
    counter.tell(new Count(), null);

    // check that it has counted correctly
    FiniteDuration duration = FiniteDuration.create(3, TimeUnit.SECONDS);
    Future<Object> result = ask(counter, new Get(),
      Timeout.durationToTimeout(duration));
    assertEquals(3, Await.result(result, duration));

    // shut down the actor system
    system.shutdown();
    system.awaitTermination();
  }
}
