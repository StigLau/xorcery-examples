CREATE CONSTRAINT PostId IF NOT EXISTS FOR (node:Post) REQUIRE node.id IS UNIQUE;
CREATE CONSTRAINT CommentId IF NOT EXISTS FOR (node:Comment) REQUIRE node.id IS UNIQUE;
