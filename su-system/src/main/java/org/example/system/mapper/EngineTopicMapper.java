package org.example.system.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EngineTopicMapper {

    @Delete("DELETE FROM engine_topic WHERE engine_id = #{engineId}")
    void deleteByEngineId(@Param("engineId") String engineId);

    @Insert("INSERT INTO engine_topic (id, engine_id, topic_id) VALUES (#{id}, #{engineId}, #{topicId})")
    void insert(@Param("id") String id, @Param("engineId") String engineId, String topicId);
}
