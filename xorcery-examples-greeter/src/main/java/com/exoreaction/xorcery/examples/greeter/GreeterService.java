package com.exoreaction.xorcery.examples.greeter;

import dev.xorcery.configuration.Configuration;
import dev.xorcery.configuration.InstanceConfiguration;
import dev.xorcery.domainevents.api.MetadataEvents;
import dev.xorcery.neo4jprojections.api.Neo4jProjections;
import dev.xorcery.neo4jprojections.api.ProjectionStreamContext;
import dev.xorcery.reactivestreams.api.server.ServerWebSocketStreams;
import dev.xorcery.jsonapi.service.ServiceResourceObject;
import dev.xorcery.jsonapi.service.ServiceResourceObjects;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.glassfish.hk2.api.PreDestroy;
import org.glassfish.hk2.runlevel.RunLevel;
import org.jvnet.hk2.annotations.Service;
import reactor.core.Disposable;
import reactor.util.context.Context;

@Service
@Named(GreeterApplication.SERVICE_TYPE)
@RunLevel(20)
public class GreeterService
    implements PreDestroy
{
    private final Disposable subscriber;

    @Inject
    public GreeterService(ServiceResourceObjects serviceResourceObjects,
                          Configuration configuration,
                          ServerWebSocketStreams serverWebSocketStreams,
                          Neo4jProjections neo4jProjections) {

        subscriber = serverWebSocketStreams.subscriberWithResult("projections/greeter", MetadataEvents.class, MetadataEvents.class,
                flux -> flux.transformDeferredContextual(neo4jProjections.projection()).contextWrite(Context.of(ProjectionStreamContext.projectionId, "greeter")));

        serviceResourceObjects.add(new ServiceResourceObject.Builder(InstanceConfiguration.get(configuration), "greeter")
                .version("1.0.0")
                .attribute("domain", "greeter")
                .api("greeter", "api/greeter")
                .build());
    }

    @Override
    public void preDestroy() {
        subscriber.dispose();
    }
}
