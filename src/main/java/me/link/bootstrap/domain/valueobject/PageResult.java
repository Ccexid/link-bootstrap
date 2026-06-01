package me.link.bootstrap.domain.valueobject;

import java.util.List;

public record PageResult<T>(List<T> records, Long total) {
}
