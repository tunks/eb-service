package com.att.kepler.ssot.dao;

import java.util.List;

public interface DeleteOperations<ID> {
    public void delete(ID id);
    public void deleteAll(List<ID> id);
}
