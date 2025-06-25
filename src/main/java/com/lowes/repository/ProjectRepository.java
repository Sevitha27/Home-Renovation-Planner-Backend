package com.lowes.repository;

import com.lowes.entity.Phase;
import com.lowes.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {

}
