package com.parthparekh.api;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Product document for mongoDB serialization
 *
 * @author: Parth Parekh
 **/
@Document
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
                getterVisibility = JsonAutoDetect.Visibility.NONE,
                setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonPropertyOrder({
        "id",
        "name",
        "description",
        "status",
        "price"
})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Product implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull(groups=PUT.class)
    protected String id;

    @Indexed
    @NotNull(groups=POST.class)
    protected String name;

    @NotNull(groups=POST.class)
    protected String description;

    protected ProductStatus status;

    @Indexed
    @NotNull(groups=POST.class)
    protected BigDecimal price;


    public Product() {
        // default status is always ACTIVE
        status = ProductStatus.ACTIVE;
    }

    public Product(String productName, String description, ProductStatus status, BigDecimal price) {
        this.name = productName;
        this.description = description;
        this.status = status;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public Product setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public Product setStatus(ProductStatus status) {
        this.status = status;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Product setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (description != null ? !description.equals(product.description) : product.description != null) return false;
        if (id != null ? !id.equals(product.id) : product.id != null) return false;
        if (price != null ? !price.equals(product.price) : product.price != null) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        if (status != product.status) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public Product clone() {
        try {
            return (Product) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}