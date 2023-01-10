package com.example.pureoauth2gradle.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity(name = "tbl_role3")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    private String roleName;
    private String roleDescription;
}
