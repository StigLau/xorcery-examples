package com.exoreaction.xorcery.service.forum.contexts;

import com.exoreaction.xorcery.metadata.Metadata;
import com.exoreaction.xorcery.service.domainevents.api.context.DomainContext;
import com.exoreaction.xorcery.service.domainevents.api.entity.Command;
import com.exoreaction.xorcery.service.forum.ForumApplication;
import com.exoreaction.xorcery.service.forum.entities.CommentEntity;
import com.exoreaction.xorcery.service.forum.model.PostModel;
import com.exoreaction.xorcery.util.UUIDs;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static com.exoreaction.xorcery.service.domainevents.api.DomainEventMetadata.Builder.aggregate;

public record PostCommentsContext(ForumApplication forumApplication, PostModel postModel)
        implements DomainContext {

    @Override
    public List<Command> commands() {
        return List.of(new CommentEntity.AddComment(UUIDs.newId(), ""));
    }

    @Override
    public CompletionStage<Metadata> handle(Metadata metadata, Command command) {
        return forumApplication.handle(new CommentEntity(), aggregate("PostAggregate", postModel.getAggregateId(), metadata), command);
    }
}
