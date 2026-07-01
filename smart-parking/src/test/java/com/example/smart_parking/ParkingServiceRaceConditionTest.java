package com.example.smart_parking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ParkingServiceRaceConditionTest {

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @Mock
    private ParkingSlotView parkingSlotView;

    @Mock
    private ParkingEventLogger parkingEventLogger;

    @InjectMocks
    private ParkingService parkingService;

    private AutoCloseable closeable;

    @Before
    public void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testAddSlotConcurrent() {
        List<ParkingSlot> slots = new ArrayList<>();
        ParkingSlot slot = new ParkingSlot("1");
        when(parkingSlotRepository.findById(anyString()))
            .thenAnswer(invocation -> slots.stream()
                .findFirst().orElse(null));
        doAnswer(invocation -> {
            slots.add(slot);
            return null;
        }).when(parkingSlotRepository).save(any(ParkingSlot.class));
        List<Thread> threads = IntStream.range(0, 10)
            .mapToObj(i -> new Thread(() -> parkingService.addSlot(slot)))
            .peek(t -> t.start())
            .collect(Collectors.toList());
        await().atMost(10, SECONDS)
            .until(() -> threads.stream().noneMatch(t -> t.isAlive()));
        assertThat(slots).containsExactly(slot);
    }
}