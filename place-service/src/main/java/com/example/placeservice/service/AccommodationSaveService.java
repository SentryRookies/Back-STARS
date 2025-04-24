package com.example.placeservice.service;

import com.example.placeservice.dto.AccommodationResponse;
import com.example.placeservice.entity.Area;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccommodationSaveService {
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    public AccommodationSaveService(JdbcTemplate jdbcTemplate, EntityManager entityManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
    }

    @Transactional
    public void saveAccommodation(List<AccommodationResponse.Body.Items.Item> items) {
        String sql = "INSERT INTO accommodation (area_id, content_id, category, name, address, lat, lon, phone, sigungu_code, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        List<Area> allAreas = entityManager.createQuery("SELECT a FROM Area a", Area.class).getResultList();

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                AccommodationResponse.Body.Items.Item item = items.get(i);

                Long areaId = findClosestAreaInMemory(new BigDecimal(item.getMapy()), new BigDecimal(item.getMapx()), allAreas);
                ps.setObject(1, areaId);

                // content_id
                try {
                    ps.setLong(2, Long.parseLong(item.getContentid()));
                } catch (NumberFormatException e) {
                    ps.setNull(2, Types.BIGINT);
                }

                // category
                // 소분류 코드
                // 0100 : 관광호텔 / 0500 : 콘도미니엄 / 0600 : 유스호스텔 / 0700 : 펜션 / 0900 : 모텔 / 1100 : 게하 / 1600 : 한옥
                try {
                    switch (item.getCat3().substring(5)) {
                        case "0100":
                            ps.setString(3, "관광호텔"); break;
                        case "0500":
                            ps.setString(3, "콘도미니엄"); break;
                        case "0600":
                            ps.setString(3, "유스호스텔"); break;
                        case "0700":
                            ps.setString(3, "펜션"); break;
                        case "0900":
                            ps.setString(3, "모텔"); break;
                        case "1100":
                            ps.setString(3, "게스트하우스"); break;
                        case "1600":
                            ps.setString(3, "한옥"); break;
                        default:
                            ps.setString(3, "구분없음"); break;
                    }
                } catch (NumberFormatException e) {
                    ps.setNull(3, Types.VARCHAR);
                }

                ps.setString(4, item.getTitle());
                ps.setString(5, item.getAddr1());

                // lat
                try {
                    if (item.getMapy() != null && !item.getMapy().isEmpty()) {
                        ps.setBigDecimal(6, new BigDecimal(item.getMapy()));
                    } else {
                        ps.setNull(6, Types.NUMERIC);
                    }
                } catch (NumberFormatException e) {
                    ps.setNull(6, Types.NUMERIC);
                }

                // lon
                try {
                    if (item.getMapx() != null && !item.getMapx().isEmpty()) {
                        ps.setBigDecimal(7, new BigDecimal(item.getMapx()));
                    } else {
                        ps.setNull(7, Types.NUMERIC);
                    }
                } catch (NumberFormatException e) {
                    ps.setNull(7, Types.NUMERIC);
                }

                ps.setString(8, item.getTel());
                ps.setString(9, item.getSigungucode());

                if (item.getFirstimage() != null && !item.getFirstimage().isEmpty()) {
                    ps.setString(10, item.getFirstimage());
                } else {
                    ps.setNull(10, Types.VARCHAR);
                }
            }

            @Override
            public int getBatchSize() {
                return items.size();
            }
        });
    }

    private Long findClosestAreaInMemory(BigDecimal lat, BigDecimal lon, List<Area> areas) {
        double minDistance = 8000.0;
        Long closestAreaId = null;

        for (Area area : areas) {
            if (area.getLat() == null || area.getLon() == null) {
                System.out.println("지역 좌표 오류 : 지역번호 " + area.getAreaId());
                continue;
            }
            double distance = calculateHaversineDistance(
                lat.doubleValue(), lon.doubleValue(),
                area.getLat().doubleValue(), area.getLon().doubleValue()
            );
            if (Double.isNaN(distance)) {
                System.out.println("거리가 숫자가 아닙니다 : 지역번호 " + area.getAreaId());
                continue;
            }
            if (distance < minDistance) {
                minDistance = distance;
                closestAreaId = area.getAreaId();
            }
        }

        return closestAreaId;
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        // 지구 반지름 (km)
        final double R = 6371.0;

        // 라디안으로 변환
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // 하버사인 공식
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리 계산 (km)
        return R * c;
    }
}