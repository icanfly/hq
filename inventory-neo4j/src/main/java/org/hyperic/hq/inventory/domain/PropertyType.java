package org.hyperic.hq.inventory.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hyperic.hq.reference.RelationshipTypes;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.graph.annotation.GraphProperty;
import org.springframework.data.graph.annotation.NodeEntity;
import org.springframework.data.graph.annotation.RelatedTo;
import org.springframework.data.graph.core.Direction;
import org.springframework.data.graph.neo4j.support.GraphDatabaseContext;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Configurable
@NodeEntity(partial = true)
public class PropertyType {
    @GraphProperty
    @Transient
    private String defaultValue;

    @NotNull
    @GraphProperty
    @Transient
    private String description;

    @PersistenceContext
    transient EntityManager entityManager;
    
    @javax.annotation.Resource
    private transient GraphDatabaseContext graphDatabaseContext;

    @Id
    @GenericGenerator(name = "mygen1", strategy = "increment")
    @GeneratedValue(generator = "mygen1")
    @Column(name = "id")
    private Integer id;

    @NotNull
    @GraphProperty
    @Transient
    private String name;

    @GraphProperty
    @Transient
    private Boolean optional;

    @ManyToOne
    @Transient
    @RelatedTo(type = RelationshipTypes.HAS_PROPERTY_TYPE, direction = Direction.INCOMING, elementClass = ResourceType.class)
    private ResourceType resourceType;

    @GraphProperty
    @Transient
    private Boolean secret;
    
    @GraphProperty
    @Transient
    private Boolean hidden;
    
    @GraphProperty
    @Transient
    private boolean indexed;

    @Version
    @Column(name = "version")
    private Integer version;

    public PropertyType() {

    }

    @Transactional
    public void flush() {
        this.entityManager.flush();
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Boolean isOptional() {
        //TODO proper way to do default value?
       if(this.optional == null) {
           return false;
       }
       return this.optional;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    public Boolean isSecret() {
        if(this.secret == null) {
            return false;
        }
        return this.secret;
    }

    public Integer getVersion() {
        return this.version;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    @Transactional
    public PropertyType merge() {
        PropertyType merged = this.entityManager.merge(this);
        this.entityManager.flush();
        merged.getId();
        return merged;
    }

    @Transactional
    public void remove() {
        graphDatabaseContext.removeNodeEntity(this);
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            PropertyType attached = this.entityManager.find(this.getClass(), this.id);
            this.entityManager.remove(attached);
        }
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public void setSecret(Boolean secret) {
        this.secret = secret;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public boolean isHidden() {
        if(this.hidden == null) {
            return false;
        }
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Version: ").append(getVersion()).append(", ");
        sb.append("ResourceType: ").append(getResourceType()).append(", ");
        sb.append("Name: ").append(getName()).append(", ");
        sb.append("Description: ").append(getDescription()).append(", ");
        sb.append("Optional: ").append(isOptional()).append(", ");
        sb.append("Secret: ").append(isSecret()).append(", ");
        sb.append("DefaultValue: ").append(getDefaultValue());
        sb.append("Hidden: ").append(isHidden());
        return sb.toString();
    }
}