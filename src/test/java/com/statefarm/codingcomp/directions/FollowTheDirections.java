package com.statefarm.codingcomp.directions;

import com.statefarm.codingcomp.configuration.CodingCompetitionConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CodingCompetitionConfiguration.class)
public class FollowTheDirections {
	@Autowired
	private String stateFarmFilesPath;
	
	@Test
	public void didYouFollowTheDirections() {
		System.out.println(stateFarmFilesPath);
		assertTrue("You didn't unzip the file.", new File(stateFarmFilesPath).exists());
	}
}
