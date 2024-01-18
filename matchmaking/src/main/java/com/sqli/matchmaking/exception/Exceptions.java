package com.sqli.matchmaking.exception;


public final class Exceptions {

    public static final class EntityNotFound extends RuntimeException {
        public EntityNotFound(String entityName, String selection, Object value) {
            super(entityName + " with " + selection + " \"" + value + "\" does not exist");
        }
    }

    public static final class EntityCannotBeSaved extends RuntimeException {
        public EntityCannotBeSaved(String entityName) {
            super(entityName + "cannot be saved");
        }
    }

    public static final class EntityCannotBeDeleted extends RuntimeException {
        public EntityCannotBeDeleted(String entityName) {
            super(entityName + "cannot be deleted");
        }
    }

    public static final class EntityCannotBeUpdated extends RuntimeException {
        public EntityCannotBeUpdated(String entityName, String field) {
            super(entityName + "cannot be updated by " + field);
        }
    }

    public static final class TwoEntitiesLinkNotFound extends RuntimeException {
        public TwoEntitiesLinkNotFound(String entity1, String entity2, Long e1Id, Long e2Id) {
            super("Cannot find link between " + entity1 + " of id : " + e1Id + " and " + 
                entity2 + " of id : " + e2Id );
        }
    }

    public static final class OnlyAdmin extends RuntimeException {
        public OnlyAdmin() {
            super("User is not a admin");
        }
    }

    public static final class OnlyOrganizerAndAdmin extends RuntimeException {
        public OnlyOrganizerAndAdmin() {
            super("User is not the organizer or an admin");
        }
    }

    public static final class MatchMustBeOnStatus extends RuntimeException {
        public MatchMustBeOnStatus(String status, Boolean bool) {
            super("Match is" + (bool == true ? " not " : " ") + status);
        }
    }

}