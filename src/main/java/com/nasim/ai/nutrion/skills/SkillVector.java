package com.nasim.ai.nutrion.skills;

import com.nasim.ai.nutrion.util.Vector;

 public class SkillVector extends Vector {
    private final Skill skill;
     public SkillVector(double[] vector, Skill skill) {
         super(vector);
         this.skill=skill;
     }
     public Skill getSkill() {
         return skill;
     }

 }