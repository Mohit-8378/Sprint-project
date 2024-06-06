import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { vi, describe, it, expect } from 'vitest';
import CourseComponent from '../components/CourseComponent';
import { findCoursesByProgramId, findProgramById } from '../Services/CourseService';

// Mock the services
vi.mock('../Services/CourseService', () => ({
  findCoursesByProgramId: vi.fn(),
  findProgramById: vi.fn(),
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: () => ({ programId: '123' }),
  };
});

describe('CourseComponent', () => {
 

  it('renders no courses message when no courses are found', async () => {
    findProgramById.mockResolvedValueOnce({ data: { programName: 'Test Program' } });
    findCoursesByProgramId.mockResolvedValueOnce({ data: [] });

    render(
      <BrowserRouter>
        <CourseComponent />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('No Course Found in Test Program')).toBeInTheDocument();
    });
  });

  it('navigates to branch info when "View Branches" is clicked', async () => {
    findProgramById.mockResolvedValueOnce({ data: { programName: 'Test Program' } });
    findCoursesByProgramId.mockResolvedValueOnce({ data: [
      { courseId: '1', courseName: 'Course 1', eligibility: 'Eligibility 1', duration: '1 Year' },
    ]});

    render(
      <BrowserRouter>
        <CourseComponent />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('View Branches')).toBeInTheDocument();
    });

    const viewBranchesButton = screen.getByText('View Branches');
    viewBranchesButton.click();
    expect(mockNavigate).toHaveBeenCalledWith('/branch/1');
  });

  it('navigates to apply now when "Apply Now" is clicked', async () => {
    findProgramById.mockResolvedValueOnce({ data: { programName: 'Test Program' } });
    findCoursesByProgramId.mockResolvedValueOnce({ data: [
      { courseId: '1', courseName: 'Course 1', eligibility: 'Eligibility 1', duration: '1 Year' },
    ]});

    render(
      <BrowserRouter>
        <CourseComponent />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Apply Now')).toBeInTheDocument();
    });

    const applyNowButton = screen.getByText('Apply Now');
    applyNowButton.click();
    expect(mockNavigate).toHaveBeenCalledWith('/programScheduledByCourse/1');
  });
});
