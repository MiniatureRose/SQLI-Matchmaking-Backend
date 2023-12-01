package com.sqli.matchmaking.model.composite;

import com.sqli.matchmaking.model.Field;
import com.sqli.matchmaking.model.Sport;

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
public class FieldSport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "field_id")
    private Field field;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sport_id")
    private Sport sport;

}