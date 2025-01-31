package com.exoreaction.xorcery.examples.forum.resources.api;

import com.exoreaction.xorcery.domainevents.helpers.context.DomainEventMetadata;
import com.exoreaction.xorcery.domainevents.helpers.entity.Command;
import com.exoreaction.xorcery.examples.forum.contexts.PostsContext;
import com.exoreaction.xorcery.jsonapi.Included;
import com.exoreaction.xorcery.jsonapi.Links;
import com.exoreaction.xorcery.jsonapi.ResourceDocument;
import com.exoreaction.xorcery.jsonapi.ResourceObject;
import com.exoreaction.xorcery.jsonapi.server.resources.JsonApiResource;
import com.exoreaction.xorcery.metadata.Metadata;
import com.exoreaction.xorcery.examples.forum.ForumApplication;
import com.exoreaction.xorcery.examples.forum.resources.ForumApiMixin;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.concurrent.CompletionStage;

import static com.exoreaction.xorcery.jsonapi.MediaTypes.APPLICATION_JSON_API;

@Path("api/forum/posts")
public class PostsResource
        extends JsonApiResource
        implements ForumApiMixin {

    private final PostsContext context;

    @Inject
    public PostsResource(ForumApplication forumApplication) {
        context = forumApplication.posts();
    }

    @GET
    public CompletionStage<ResourceDocument> get(@QueryParam("rel") String rel) {
        if (rel != null) {
            return commandResourceDocument(rel, null, context);
        } else {
            Links.Builder links = new Links.Builder();
            Included.Builder included = new Included.Builder();
            return posts(included, links)
                    .thenApply(ros -> new ResourceDocument.Builder()
                            .data(ros)
                            .included(included.build())
                            .links(links.with(commands(getRequestUriBuilder(), context), schemaLink()))
                            .build());
        }
    }

    @POST
    @Consumes({"application/x-www-form-urlencoded", APPLICATION_JSON_API})
    public CompletionStage<Response> post(ResourceObject resourceObject) {
        return execute(resourceObject, context, metadata());
    }

    @Override
    public CompletionStage<Response> ok(Metadata metadata, Command command) {
        String aggregateId = new DomainEventMetadata(metadata).getAggregateId();
        URI location = getUriBuilderFor(PostResource.class).build(aggregateId);
        return post(aggregateId, new Included.Builder())
                .thenApply(post -> Response.created(location).links(schemaHeader()).entity(post).build());
    }
}
