package ru.practicum.ewm.locations.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.locations.dto.LocationEventShortDto;
import ru.practicum.ewm.locations.dto.LocationDto;
import ru.practicum.ewm.locations.dto.LocationMapper;
import ru.practicum.ewm.locations.dto.NewLocationDto;
import ru.practicum.ewm.locations.model.Location;
import ru.practicum.ewm.locations.repository.LocationRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final EntityManager entityManager;

    @Override
    public LocationDto create(NewLocationDto newLocationDto) {
        Location location = LocationMapper.toLocation(newLocationDto);
        locationRepository.save(location);
        return LocationMapper.toLocationDto(location);
    }

    @Override
    public List<LocationDto> getAll() {
        return locationRepository.findAll().stream().map(LocationMapper::toLocationDto).toList();
    }

    @Override
    public LocationDto get(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location with id:" + id + " not found"));

        return LocationMapper.toLocationDto(location);
    }

    @Override
    public void delete(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new NotFoundException("Location with id:" + id + " not found");
        }
        locationRepository.deleteById(id);
    }

    @Override
    public List<LocationEventShortDto> getEvents(Long locId) {
        Location location = locationRepository.findById(locId)
                .orElseThrow(() -> new NotFoundException("Location with id:" + locId + " not found"));

        return findPublishedEventsNear(location).stream()
                .map(LocationServiceImpl::toLocationEventShortDto)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> findPublishedEventsNear(Location location) {
        return entityManager.createNativeQuery("""
                        SELECT e.id, e.title, e.annotation, e.event_date, e.paid
                        FROM events e
                        WHERE e.state = 'PUBLISHED'
                          AND earth_distance(
                                ll_to_earth(e.lat, e.lon),
                                ll_to_earth(:lat, :lon)
                              ) <= :radius
                        """)
                .setParameter("lat", location.getLat())
                .setParameter("lon", location.getLon())
                .setParameter("radius", location.getRadiusMeters())
                .getResultList();
    }

    private static LocationEventShortDto toLocationEventShortDto(Object[] row) {
        LocationEventShortDto dto = new LocationEventShortDto();
        dto.setId(((Number) row[0]).longValue());
        dto.setTitle((String) row[1]);
        dto.setAnnotation((String) row[2]);
        dto.setEventDate(toLocalDateTime(row[3]));
        dto.setPaid((Boolean) row[4]);
        return dto;
    }

    private static LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        return (LocalDateTime) value;
    }
}
