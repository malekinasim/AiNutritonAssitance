package com.nasim.ai.nutrion.skills;

import com.nasim.ai.nutrion.util.VectorEntry;

 public class SkillVectorEntry extends VectorEntry {
    private final Skill skill;
     public SkillVectorEntry(double[] vector, Skill skill) {
         super(vector);
         this.skill=skill;
     }
     public Skill getSkill() {
         return skill;
     }

 }