package com.example.hw8;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class SearchPostsUnitTest {
    private List<Post> postList;
    private List<Post> copyList;
    private MainActivity mock;
    private String title;
    private String notTitle;

    @Before
    public void setUp() {
        mock = new MainActivity();
        postList = new ArrayList<>();
        copyList = new ArrayList<>();
        title = "Title";
        notTitle = "228";
        for (int i = 0; i < 10; ++i) {
            postList.add(new Post(i, 0, title + i, Integer.toString(i)));
        }
        copyList.addAll(postList);
    }

    @After
    public void tearDown() {
        postList = null;
        copyList = null;
    }

    private void searchWith(String filter) {
        List<Post> filtered = mock.searchPosts(filter, postList);
        for (Post p : filtered) {
            assertTrue(p.getTitle().contains(filter));
        }
    }

    @Test
    public void searchNotChangesPostList() {
        mock.searchPosts("anything", postList);
        assertThat(copyList, is(postList));
    }

    @Test
    public void search0() {
        searchWith("0");
    }

    @Test
    public void search0IsOne() {
        assertEquals(1, mock.searchPosts("0", postList).size());
    }

    @Test
    public void searchTitle() {
        searchWith(title);
    }

    @Test
    public void searchTitleIsAll() {
        assertEquals(postList.size(), mock.searchPosts(title, postList).size());
    }

    @Test
    public void searchNotTitleIsNothing() {
        assertTrue(mock.searchPosts(notTitle, postList).isEmpty());
    }

    @Test
    public void searchNotChangesPosts() {
        List<Post> filtered = mock.searchPosts(title, postList);
        for (Post p : filtered) {
            assertTrue(postList.contains(p));
        }
    }

    @Test
    public void searchNotChangesOrder() {
        List<Post> filtered = mock.searchPosts(title, postList);
        int prevIndex = -1;
        for (Post p : filtered) {
            int curIndex = filtered.indexOf(p);
            assertTrue(prevIndex < curIndex);
            prevIndex = curIndex;
        }
    }
}
