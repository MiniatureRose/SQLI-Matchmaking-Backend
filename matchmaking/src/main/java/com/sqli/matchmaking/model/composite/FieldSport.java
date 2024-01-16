package com.sqli.matchmaking.model.composite;

import com.sqli.matchmaking.model.standalone.Field;
import com.sqli.matchmaking.model.standalone.Sport;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "fieldsports", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"field_id", "sport_id"})
})
public final class FieldSport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "field_id")
    private Field field;

    @ManyToOne()
    @JoinColumn(name = "sport_id")
    private Sport sport;

}